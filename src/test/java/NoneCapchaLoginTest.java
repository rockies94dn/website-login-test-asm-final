import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class NoneCapchaLoginTest extends WebsiteLoginTest {

    // Lấy các element cần thao tác trên trang
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");
    private final By pageTitle = By.cssSelector(".title");

    private WebDriverWait wait;

    @BeforeMethod
    public void initWait() {
        driver.get("https://www.saucedemo.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /***
     * Thao tác nhập tên đăng nhập, mật khẩu và nhấn nút đăng nhập
     * @param username
     * @param password
     */
    private void enterCredentialsAndSubmit(String username, String password) {
        WebElement user = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        WebElement pass = driver.findElement(passwordField);

        user.clear();
        if (!username.isEmpty()) {
            user.sendKeys(username);
        }

        pass.clear();
        if (!password.isEmpty()) {
            pass.sendKeys(password);
        }

        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    // TC01: Đăng nhập thành công
    @Test(priority = 1)
    public void loginWithValidAccount() {
        enterCredentialsAndSubmit("standard_user", "secret_sauce");

        // Xác nhận URL chuyển sang trang inventory
        boolean isRedirected = wait.until(ExpectedConditions.urlContains("inventory.html"));
        Assert.assertTrue(isRedirected, "FAIL: Không chuyển hướng sang trang sản phẩm.");

        // Xác nhận có chữ "Products" trên góc trái màn hình
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        Assert.assertEquals(title.getText(), "Products", "FAIL: Sai tiêu đề trang.");
    }

    // TC02: Sai mật khẩu
    @Test(priority = 2)
    public void loginWithInvalidPassword() {
        enterCredentialsAndSubmit("standard_user", "wrong_password");

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));

        Assert.assertTrue(error.isDisplayed(), "Failed: Không hiện thông báo lỗi.");
        Assert.assertEquals(error.getText(),
                "Epic sadface: Username and password do not match any user in this service",
                "Failed: Sai nội dung thông báo lỗi.");
    }

    // TC03: Bỏ trống dữ liệu
    @Test(priority = 3)
    public void loginWithEmptyData() {
        enterCredentialsAndSubmit("", ""); // Không truyền gì cả

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));

        Assert.assertTrue(error.isDisplayed(), "Failed: Không hiện thông báo lỗi.");
        Assert.assertEquals(error.getText(),
                "Epic sadface: Username is required",
                "Failed: Sai nội dung thông báo lỗi khi bỏ trống.");
    }

    //     TC04: Đăng nhập với tài khoản bị khóa
    @Test(priority = 4)
    public void loginWithLockedOutUser() {
        enterCredentialsAndSubmit("locked_out_user", "secret_sauce");

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));

        Assert.assertTrue(error.isDisplayed(), "Failed: Không hiện thông báo lỗi.");
        Assert.assertEquals(error.getText(),
                "Epic sadface: Sorry, this user has been locked out.",
                "Failed: Sai nội dung báo lỗi user bị khóa.");
    }

    // TC05: Nhập Username nhưng bỏ trống Password
    @Test(priority = 5)
    public void loginWithUsernameOnly() {
        // Chỉ nhập username, để trống password
        enterCredentialsAndSubmit("standard_user", "");

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));

        Assert.assertTrue(error.isDisplayed(), "Failed: Không hiện thông báo lỗi.");
        Assert.assertEquals(error.getText(),
                "Epic sadface: Password is required",
                "Failed: Nội dung lỗi không đúng khi thiếu Password.");
    }

    // TC06: Kiểm tra hiệu năng
    @Test(priority = 6)
    public void loginWithPerformanceGlitchUser() {
        long startTime = System.currentTimeMillis();

        enterCredentialsAndSubmit("performance_glitch_user", "secret_sauce");
        boolean isRedirected = wait.until(ExpectedConditions.urlContains("inventory.html"));
        long endTime = System.currentTimeMillis();
        long durationInSeconds = (endTime - startTime) / 1000;

        Assert.assertTrue(isRedirected, "Failed: Đăng nhập không thành công sau khi chờ đợi.");

        // Xác nhận trang web bị delay ít nhất 5 giây
        System.out.println("Thời gian đăng nhập thực tế: " + durationInSeconds + " giây.");
        Assert.assertTrue(durationInSeconds >= 5,
                "Failed: Thời gian phản hồi quá nhanh (" + durationInSeconds + "s), không đúng với thiết kế của user này.");
        // Xác nhận tiêu đề trang xuất hiện
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        Assert.assertEquals(title.getText(), "Products", "Failed: Sai tiêu đề trang sau khi login chậm.");
    }
}