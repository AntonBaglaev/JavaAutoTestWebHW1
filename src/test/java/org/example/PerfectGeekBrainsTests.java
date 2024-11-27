package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerfectGeekBrainsTests {
    private WebDriver driver;
    private WebDriverWait wait;
    private static String USERNAME;
    private static String PASSWORD;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        USERNAME = System.getProperty("geekbrains_username", System.getenv("geekbrains_username"));
        PASSWORD = System.getProperty("geekbrains_password", System.getenv("geekbrains_password"));
    }

    @BeforeEach
    public void setupTest() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testStandGeekBrains() throws IOException {
        driver.get("https://test-stand.gb.ru/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form#login input[type='text']"))).sendKeys(USERNAME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form#login input[type='password']"))).sendKeys(PASSWORD);

        WebElement loginButton = driver.findElement(By.cssSelector("form#login button"));
        loginButton.click();
        wait.until(ExpectedConditions.invisibilityOf(loginButton));

        WebElement usernameLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(USERNAME)));
        assertEquals(String.format("Hello, %s", USERNAME), usernameLink.getText().replace("\n", " ").trim());
        //getScreen();
    }

    @Test
    void groupAddingTest() throws  IOException {
        testStandGeekBrains();
        String groupName = "New Study Group " + System.currentTimeMillis();
        WebElement createGroup = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id='create-btn']")));
        createGroup.click();
        By fieldGroupName = By.xpath("//*[@type='text']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(fieldGroupName)).sendKeys(groupName);
        WebElement buttonSaveGroup = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form div.submit button")));
        buttonSaveGroup.click();

        String tableTitleXpath = "//td[contains(text(), '%s')]";
        WebElement expectedTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(String.format(tableTitleXpath, groupName))));
        Assertions.assertTrue(expectedTitle.isDisplayed());
        getScreen();
    }

    private void getScreen() throws IOException {
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(Path.of(
                "src/main/resources/screenshot_" + System.currentTimeMillis() + ".png"), screenshotBytes);
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }
}
