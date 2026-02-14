package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CookieHandler {
    private WebDriver driver;

    public CookieHandler(WebDriver driver) {
        this.driver = driver;
    }

    public void handleCookieBanner() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // Таймаут 10 сек

        try {
            // Ждем, пока элемент станет кликабельным (если он появится)
            WebElement acceptButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(
                            "//div[(text()='Allow essential cookies') and @id='gdpr-popup-v3-button-mandatory']"))  // Ваш селектор
            );
            acceptButton.click();
            System.out.println("Куки приняты!");
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Баннер куки не появился — продолжаем.");
            // Здесь ничего не делаем, скрипт идет дальше
        }
    }
}