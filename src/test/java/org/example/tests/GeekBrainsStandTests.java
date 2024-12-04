package org.example.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.example.MainPage;
import org.example.ProfilePage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.example.LoginPage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeekBrainsStandTests {
    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private MainPage mainPage;
    private static String USERNAME;
    private static String PASSWORD;

    @BeforeAll
    public static void setupClass() {
        USERNAME = System.getProperty("geekbrains_username", System.getenv("geekbrains_username"));
        PASSWORD = System.getProperty("geekbrains_password", System.getenv("geekbrains_password"));
    }

    @BeforeEach
    public void setupTest() {
        Selenide.open("https://test-stand.gb.ru/login");
        driver = WebDriverRunner.getWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://test-stand.gb.ru/login");
        loginPage = new LoginPage(driver, wait);
    }

    @Test
    public void testAddingGroupOnMainPage() {
        checkLogin();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
    }

    @Test
    void testArchiveGroupOnMainPage() {
        checkLogin();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Требуется закрыть модальное окно
        mainPage.closeCreateGroupModalWindow();
        // Изменение созданной группы с проверками
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("inactive", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickRestoreFromTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
    }

    @Test
    void authorizationWithoutEnteringLoginAndPasswordShouldReturnTest() throws IOException {
        loginPage.clickLoginButton();
        assertEquals("401 Invalid credentials.", loginPage.getErrorBlockText());
        getScreen();
    }

    @Test
    void studentStatusActiveOrInactiveTest() throws IOException {
        String groupName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupName);
        mainPage.closeCreateGroupModalWindow();
        int studentQuantity = 2;
        mainPage.clickOnCreatingNewLoginsStudentsByTitle(groupName);
        mainPage.enteringTheNumberOfNewLoginsStudents(studentQuantity);
        mainPage.clickSaveNumberNewLoginsStudents();
        mainPage.clickCloseNewLoginsStudentsForm();
        mainPage.waitForChangeNumberOfLoginsStudents(groupName, studentQuantity);
        mainPage.clickOnStudentsIdentitiesByTitle(groupName);
        int studentIndex = 0;
        String studentUsername = mainPage.getStudentUsernameByIndex(studentIndex);
        assertEquals("active", mainPage.getStatusOfStudentByUsername(studentUsername));
        mainPage.clickTrashIconOnStudentByUsername(studentUsername);
        assertEquals("block", mainPage.getStatusOfStudentByUsername(studentUsername));
        mainPage.clickRestoreFromTrashIconOnStudentByUsername(studentUsername);
        assertEquals("active", mainPage.getStatusOfStudentByUsername(studentUsername));
        getScreen();
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

    @Test
    void testFullNameOnProfilePage() {
        loginPage.login(USERNAME, PASSWORD);
        mainPage = new MainPage(driver, wait);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        mainPage.clickUsernameLabel();
        mainPage.clickProfileLink();
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        assertEquals("Kornyshev Evgenii", profilePage.getFullNameFromAdditionalInfo());
        assertEquals("Kornyshev Evgenii", profilePage.getFullNameFromAvatarSection());
    }

    private void getScreen() throws IOException {
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(Path.of(
                "src/main/resources/screenshot_" + System.currentTimeMillis() + ".png"), screenshotBytes);
    }

    private void checkLogin() {
        // Логин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME, PASSWORD);
        // Инициализация объекта класса MainPage
        mainPage = new MainPage(driver, wait);
        // Проверка, что логин прошёл успешно
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
    }

    @AfterEach
    public void teardown() {
        WebDriverRunner.closeWebDriver();
    }
}
