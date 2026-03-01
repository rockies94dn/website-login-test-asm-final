import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class WebsiteLoginTest {

    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        ChromeOptions opts = new ChromeOptions();

        Map<String, Object> chromePrefs = new HashMap<>();
        // Tắt tính năng kiểm tra rò rỉ mật khẩu trong Cài đặt
        chromePrefs.put("profile.password_manager_leak_detection", false);
        opts.setExperimentalOption("prefs", chromePrefs);

//        opts.addArguments("--incognito");

        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    @BeforeMethod
    public void prepareEnvironment() {
        // Xóa cookie
        driver.manage().deleteAllCookies();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}