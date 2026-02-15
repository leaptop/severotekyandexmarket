package yandex;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static helpers.properties.Properties.mainProperties;

/**
 * Базовый класс для запуска тестов. Здесь инициализируется драйвер веб браузера, указывается поведение перед
 * каждым тестом и после каждого теста.
 */
public class BaseTest {
    /**
     * Основной веб драйвер для всех классов потомков
     */
    public WebDriver webDriver;

    /**
     * Метод для запуска перед каждым тестом. Назначает откуда брать драйвер браузера, задаёт начальное состояние
     * браузера, устанавливает таймауты.
     */
    @BeforeEach
    public void beforeEach() {
        System.setProperty(mainProperties.browser(), System.getenv(mainProperties.driver()));

        ChromeOptions options = new ChromeOptions();//All these options are an attempt to hide the fact that it is a bot
        options.addArguments("--disable-blink-features=AutomationControlled");  // Скрывает автоматизацию
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");  // Рандомный UA
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");  // Для headless
        options.addArguments("--disable-extensions");  // Отключает расширения

        webDriver = new ChromeDriver(options);
        ((JavascriptExecutor) webDriver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");  // JS-патч для скрытия webdriver

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(mainProperties.defaultTimeout30(), TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(mainProperties.defaultTimeout30(), TimeUnit.SECONDS);
        webDriver.manage().timeouts().setScriptTimeout(mainProperties.defaultTimeout30(), TimeUnit.SECONDS);
    }

    /**
     * Метод запускается после каждого теста.
     * Закрывает браузер и завершает тест.
     */
   // @AfterEach
    public void closeBrowser() {
        webDriver.quit();
    }
}
