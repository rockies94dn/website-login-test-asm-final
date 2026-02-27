import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class LazadaLoginTest extends WebsiteLoginTest {
    WebDriverWait wait;

    @BeforeMethod
    public void beforeMethod() {
        driver.get("https://pages.lazada.vn/wow/gcp/vn/member/login-signup");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }


    // TC01 – Đăng nhập hợp lệ
    @Test(priority = 1)
    public void loginWithValidAccount() throws InterruptedException {

        WebElement usernameInput = driver.findElement(By.cssSelector("input[placeholder='Please enter your Phone or Email']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='Please enter your password']"));

        usernameInput.sendKeys("0934740720");
        passwordInput.sendKeys("Sa201141@");

        WebElement loginBtn = driver.findElement(By.xpath("//button[text()='LOGIN']"));
        loginBtn.click();
        System.out.println("Giải quyết capcha bằng tay trong 30s...");
        Thread.sleep(30000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("user/profile"));
    }

    // TC02 – Sai mật khẩu
    @Test(priority = 2)
    public void loginWithInvalidPassword() throws InterruptedException {

        WebElement usernameInput = driver.findElement(By.cssSelector("input[placeholder='Please enter your Phone or Email']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='Please enter your password']"));

        usernameInput.sendKeys("0934740720");
        passwordInput.sendKeys("wrongPassword");

        WebElement loginBtn = driver.findElement(By.xpath("//button[text()='LOGIN']"));
        loginBtn.click();
        System.out.println("Giải quyết capcha bằng tay trong 30s...");
        Thread.sleep(30000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            //Chờ thông báo xuất hiện
            WebElement errorToast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".iweb-toast-wrap")));

            //Lấy nội dung text thực tế
            String actualText = errorToast.getText();
            String expectedText = "Invalid account or password.";

            //Assert kiểm tra hiển thị và nội dung
            Assert.assertTrue(errorToast.isDisplayed(), "Thông báo lỗi chưa hiển thị!");
            Assert.assertEquals(actualText, expectedText, "Nội dung thông báo lỗi không đúng!");

            System.out.println("Pass: Đã bắt được lỗi '" + actualText + "'");

        } catch (Exception e) {
            //10s mà không thấy thông báo thì fail test
            Assert.fail("Không tìm thấy thông báo lỗi. Có thể do mạng lag hoặc Locator sai.");
        }
    }

    // TC03 – Bỏ trống dữ liệu
    @Test(priority = 3)
    public void loginWithEmptyData() throws InterruptedException {

        driver.navigate().refresh();

        String urlBeforeClick = driver.getCurrentUrl();

        WebElement loginBtn = driver.findElement(By.xpath("//button[text()='LOGIN']"));
        loginBtn.click();

        System.out.println("Giải quyết capcha bằng tay trong 30s...");
        Thread.sleep(30000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        String urlAfterClick = driver.getCurrentUrl();
        Assert.assertEquals(urlAfterClick, urlBeforeClick, "FAIL: URL đã bị thay đổi, trang web đã chuyển hướng sai");
        Assert.assertFalse(urlAfterClick.contains("user/profile"), "FAIL: Hệ thống đã đăng nhập thành công vào trang user");

    }
}
