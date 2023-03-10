package baseTest;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.BasePage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BaseTest {
    private WebDriver driver;
    protected BasePage basePage;                                // protected so subclass and package can access

    // always executes before the class of the test
    @BeforeClass
    public void webDriverInit() {      
        String SELENIUM_KEY = "webdriver.chrome.driver";        // key that selenium will look for
        String CHROME_DRIVER_PATH = getChromeDriverPath();      // path for desired chromedriver
        System.setProperty(SELENIUM_KEY, CHROME_DRIVER_PATH);   // System.setProperty allows the Selenium WebDriver framework to know which driver to use for automation

        driver = new ChromeDriver(getChromeOptions());          // instantiating a new webdriver with browser options, browser options is optional

        // implicit wait, enable if needed
        // webdriver keeps X Amount Of Time allowance for the element to be found
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        basePage = new BasePage(driver);                        // homepage object that subclasses and package can access
    }

    // always executes before every @Test annotated methods
    // every test assertion starts with this page
    @BeforeMethod
    public void goToHomePage() {
        String URL_HOME_PAGE = "https://the-internet.herokuapp.com/";
        driver.get(URL_HOME_PAGE);
    }

    // always executes after every @Test annotated methods
    @AfterMethod
    public void screenShotOnFail(ITestResult testResults) {     // ITestResult gets automatically passed to the method
        if (ITestResult.FAILURE == testResults.getStatus()) {
            TakesScreenshot ss = (TakesScreenshot)driver;       // take a screenshot if test failed
            File ssFile = ss.getScreenshotAs(OutputType.FILE);  // declaring screenshot as a file object
            try {
                // moving screenshot to desired path
                String desiredSSPath = "resources/screenshots/" + testResults.getName() + ".png";
                Files.move(ssFile.toPath(), Path.of(desiredSSPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // always executes before the class of the test
    @AfterClass
    public void webDriverClose() {
        driver.quit();
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions environmentOption;

        switch(System.getProperty("os.name")) {
            case "Mac OS X" -> { environmentOption = getMacLocalMachineChromeOptions(); }
            case "Linux" -> { environmentOption = getLinuxGitHubActionChromeOptions(); }
            // defaulting with headless if os.name is not in scope
            default -> { environmentOption = getMacLocalMachineChromeOptions(); }
        }

        return environmentOption;
    }

    private ChromeOptions getLinuxGitHubActionChromeOptions() {
        ChromeOptions linuxGitHubActionChromeOptions = new ChromeOptions();
        linuxGitHubActionChromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));  // removes banner: "Chrome is being controlled by automated test software."
        linuxGitHubActionChromeOptions.addArguments("--headless");                                              // need to run headless on linux, otherwise need to setup Xvfb or X virtual framebuffer 
        return linuxGitHubActionChromeOptions;
    }

    private ChromeOptions getMacLocalMachineChromeOptions() {
        ChromeOptions macLocalMachineChromeOptions = new ChromeOptions();
        //myOptions.setHeadless(true);
        macLocalMachineChromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));   // removes banner: "Chrome is being controlled by automated test software."
        return macLocalMachineChromeOptions;
    }

    private String getChromeDriverPath() {
        String chromeDriverPath;

        switch(System.getProperty("os.name")) {
            case "Mac OS X" -> { chromeDriverPath = "resources/chromedriver108M1"; }
            case "Linux" -> { chromeDriverPath = "resources/chromedriver108Linux"; }
            default -> { chromeDriverPath = "OS is not in scope for driver path, check OS of machine and use appropriate driver."; }
        }

        return chromeDriverPath;
    }
}
