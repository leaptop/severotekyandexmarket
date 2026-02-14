package yandex;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static helpers.properties.Properties.mainProperties;

public class YandexMarket_PageObject {

    public YandexMarket_PageObject(WebDriver wd) {
        chromedriver = wd;
    }

    /**
     * Позволяет фейлить тесты без org.junit.jupiter.api.Assertions.fail
     *
     * @param condition на будущее переменная
     * @param message   бъясняем причину провала
     */
    private void assertTrueOrThrow(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    /**
     * Нажимает кнопку "Каталог"
     */
    public void pushCatalogueButton() {
        chromedriver.findElement(By.xpath("//div[@id=\"/content/header/header/catalogEntrypoint\"]")).click();
    }

    /**
     * Наводит курсор на крайний левый элемент всплывающего окна по имени name
     *
     * @param name имя элемента (кнопки)
     */
    public void selectMainPopUpElement(String name) {
        WebElement we = chromedriver.findElement(By.xpath(
                "//div[@id='catalogPopup']//ul/li/a/*[contains(text(),'" + name + "')]"));
        Actions act = new Actions(chromedriver);
        act.moveToElement(we).perform();
    }

    /**
     * Нажимает ссылку в меню справа
     *
     * @param name имя ссылки в меню справа
     */
    public void pushSecondaryMenuButton(String name) {
        chromedriver.findElement(By.xpath(
                "//div[@role='heading'and @aria-level='2']//a[text()='" + name + "']")).click();
    }

    /**
     * Вызов методов выбора производителей в зависимости от наличия/отсутствия текстового окна поиска.
     * <p>
     * В случае, когда появляется окно поиска, элементы больше не видны, если они не отображаются на мониторе
     * (колесо прокрутили и они остались вверху например). Поэтому надо использовать поиск вводом в поисковое
     * текстовое окно.
     *
     * @param m массив имён производителей для проставления галочек
     */
    public void setManufacturersUniversal(String... m) {
        chromedriver.findElement(By.xpath(showAllManufacturersButtonXPath)).click();
        waitForVisibilityOfOneElement(manufacturersWindowGeneral, mainProperties.defaultTimeout30());
        for (String mi : m) {
            chromedriver.manage().timeouts().implicitlyWait(mainProperties.timeoutShort2(), TimeUnit.SECONDS);
            if (!chromedriver.findElements(By.xpath(xpathForManufacturerInputTextField)).isEmpty()) {
                if (!chromedriver.findElements(By.xpath(clearManufacturersSearchFieldButton)).isEmpty()) {
                    chromedriver.findElement(By.xpath(clearManufacturersSearchFieldButton)).click();
                }
                chromedriver.manage().timeouts().implicitlyWait(mainProperties.defaultTimeout30(), TimeUnit.SECONDS);
                chromedriver.findElement(By.xpath(xpathForManufacturerInputTextField)).sendKeys(mi);
                WebDriverWait wait = new WebDriverWait(chromedriver, mainProperties.timeoutShort5());
                wait.until(ExpectedConditions.textToBePresentInElementValue(chromedriver.findElement(By.xpath
                        (xpathForManufacturerInputTextField)), mi));
                chromedriver.findElement(By.xpath(
                        "// label[text()='Найти производителя']/following-sibling::input/../../../following-sibling::div//span[text()='"
                                + mi + "']")).click();
            } else {
                waitForVisibilityOfOneElement(manufacturersWindowGeneral, mainProperties.defaultTimeout30());
                chromedriver.findElement(By.xpath(
                        "//div[contains(@data-zone-data,'Производитель')]/fieldset//span[text()='" + mi + "']")).click();
            }
            waitForVisibilityOfOneElement(foundNumberOfProductsTip, mainProperties.defaultTimeout30());
            chromedriver.manage().timeouts().implicitlyWait(mainProperties.timeoutShort2(), TimeUnit.SECONDS);
            if (!chromedriver.findElements(By.xpath(showAllManufacturersButtonXPath)).isEmpty()) {
                chromedriver.findElement(By.xpath(showAllManufacturersButtonXPath)).click();
                chromedriver.manage().timeouts().implicitlyWait(mainProperties.defaultTimeout30(), TimeUnit.SECONDS);
            } else {
                chromedriver.manage().timeouts().implicitlyWait(mainProperties.defaultTimeout30(), TimeUnit.SECONDS);
                continue;
            }
        }
    }

    /**
     * Устанавливает диапазон цен
     *
     * @param min цена "от"
     * @param max цена "до"
     */
    public void setPrices(int min, int max) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(chromedriver, mainProperties.defaultTimeout30());
        String minS = Integer.toString(min);
        String maxS = Integer.toString(max);
        waitForVisibilityOfOneElement(textFieldPriceMin, mainProperties.defaultTimeout30());
        // wait.until(ExpectedConditions.)

        chromedriver.findElement(By.xpath(textFieldPriceMin)).click();
        chromedriver.findElement(By.xpath(textFieldPriceMin)).sendKeys(minS);
        ;

        waitForTextToBePresent(textFieldPriceMin, minS, mainProperties.defaultTimeout30());

        waitForVisibilityOfOneElement(textFieldPriceMax, mainProperties.defaultTimeout30());
        WebElement priceMax = chromedriver.findElement(By.xpath(textFieldPriceMax));
        priceMax.sendKeys(maxS, Keys.ENTER);
        waitForTextToBePresent(textFieldPriceMax, maxS, mainProperties.defaultTimeout30());
        waitForVisibilityOfOneElement(foundNumberOfProductsTip, mainProperties.defaultTimeout30());
    }

    /**
     * Проверяет наличие хотя бы одного слова из массива m в заголовках результатов
     */
    public boolean checkManufacturers(String... m) {
        pressPGDNButton(20, "//html");
        waitForVisibilityOfAllElements("//article//h3", mainProperties.defaultTimeout30());
        List<WebElement> articles = chromedriver.findElements(By.xpath("//article"));
        for (int i = 0; i < articles.size(); i++) {
            List<WebElement> spansInArticle = articles.get(i).findElements(By.xpath(possiblyDisintegratedNameOfProductAfterSearch));
            String naimenovanie = "";
            for (int j = 0; j < spansInArticle.size(); j++) {
                naimenovanie += spansInArticle.get(j).getAttribute("textContent");
            }
            for (int k = 0; k < m.length; k++) {
                if (naimenovanie.toLowerCase().contains(m[k].toLowerCase())) {
                    break;
                } else {
                    if (k == (m.length - 1)) {
                        assertTrueOrThrow(
                                false,
                                "Наименование \"" + naimenovanie +
                                        "\" не содержит ни одного имени производителя из искомых");    //fail("Наименование \"" + naimenovanie + "\" не содержит ни одного имени производителя из искомых");
                        return false;
                    }
                    continue;
                }
            }
        }
        return true;
    }

    /**
     * Проверка цен на соответствие фильтрам
     *
     * @param min минимальная цена
     * @param max максимальная цена
     * @return true или false, в соотвтетствии с тем, соотвтетсвтуют цены фильтрам или нет.
     */
    public boolean checkPrices(int min, int max) {
        pressPGDNButton(20, "//html");
        waitForVisibilityOfAllElements("//article//h3", mainProperties.defaultTimeout30());
        List<WebElement> articles = chromedriver.findElements(By.xpath("//article"));
        for (int i = 0; i < articles.size(); i++) {
            int price = -1;
            chromedriver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
            if (!articles.get(i).findElements(By.xpath(
                    ".//div[@data-zone-name='price']//span[@data-auto='mainPrice']/span[1]")).isEmpty()) {
                price = Integer.parseInt(articles.get(i).findElement(By.xpath(
                                ".//div[@data-zone-name='price']//span[@data-auto='mainPrice']/span[1]"))
                        .getText().replaceAll("\\s+", ""));
            }
            if (price < min || price > max) {
                assertTrueOrThrow(false,
                        "Цена \"" + price +
                                "\" не соотвтетствует диапазону от " + min + " до " + max);
            }
        }
        return true;
    }

    /**
     * @param xpath   элемент, видимость которого ждём
     * @param timeout время, которое ждём в секундах
     */
    public void waitForVisibilityOfAllElements(String xpath, int timeout) {
        WebDriverWait wait = new WebDriverWait(chromedriver, timeout);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
    }

    /**
     * @param xpath            элемент, видимость которого надо ждать
     * @param timeoutInSeconds время, которое ждём в секундах
     */
    public void waitForVisibilityOfOneElement(String xpath, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(chromedriver, timeoutInSeconds);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    /**
     *
     */
    public void waitForTextToBePresent(String xpath, String text, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(chromedriver, timeoutInSeconds);
        wait.until(ExpectedConditions.textToBePresentInElementValue(By.xpath(xpath), text));
    }

    /**
     * вебдрайвер для работы с браузером.
     */
    public WebDriver chromedriver;

    /**
     * Окошко, показывающее, что определённое количество товара было найдено.
     */
    private String foundNumberOfProductsTip = "//span[@data-auto='filter-found-visible-tooltip']";

    /**
     * Текстовое поле для корректировки минимальной цены
     */
    private String textFieldPriceMin = "//div[@data-auto='filter-range-glprice']//span[@data-auto='filter-range-min']//input";
    /**
     * Текстовое поле для корректировки максимальной цены
     */
    private String textFieldPriceMax = "//div[@data-auto='filter-range-glprice']//span[@data-auto='filter-range-max']//input";
    /**
     * Кнопка "показать всё" в секции производителей.
     */
    private String showAllManufacturersButtonXPath = "//span[@role='button']/span[contains(text(),'Показать всё')]";
    /**
     * Окошко для ввода текста для поиска производителя
     */
    private String xpathForManufacturerInputTextField = "// label[text()='Найти производителя']/following-sibling::input";
    /**
     * XPath для окна производителей для обоих случаев появления окошка поиска.
     */
    private String manufacturersWindowGeneral = "//div[contains(@data-zone-data,'Производитель')]";

    /**
     * Xpath для крестика очистки поля ввода производителей
     */
    private String clearManufacturersSearchFieldButton = "//div[contains(@data-zone-data,'Производитель')" +
            "]//button[@title='Очистить']";

    /**
     * Жмёт PGDN
     *
     * @param numberOfTimes сколько раз нажать PGDN
     * @param whereToXPath  к какому элементу применить нажатие PGDN
     */
    public void pressPGDNButton(int numberOfTimes, String whereToXPath) {
        for (int i = 0; i < numberOfTimes; i++) {
            chromedriver.findElement(By.xpath(whereToXPath)).sendKeys(Keys.PAGE_DOWN);
        }
    }

    /**
     * Составляюще имени продукта после поиска через строку поиска. Получить целое имя можно с помощью применения к
     * вебэелементу //article.
     */
    private String possiblyDisintegratedNameOfProductAfterSearch = ".//h3//a/span";
}
