package yandex;

import helpers.CookieHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static helpers.properties.Properties.mainProperties;

/**
 * Класс для теста яндекс маркета
 *
 * @author Степан Алексеев
 */
public class SeverotekYandexMarket extends BaseTest {
    /**
     * Проверяет ввод цены, производителей, работу ожиданий, сохранение, сравнение элементов. Ищет ноутбуки на яндекс
     * маркете.
     *
     * @param min            минимальная цена
     * @param max            максимальная цена
     * @param mainMenu       название кнопки из главного меню
     * @param additionalMenu название кнопки из дополнительного меню
     * @param m1             производитель № 1
     */
    @ParameterizedTest(name = "{displayName} {arguments}")
    @CsvSource("25000, 30000, Компьютеры, Ноутбуки, Lenovo")
    public void mainTest(int min, int max, String mainMenu, String additionalMenu, String m1) throws InterruptedException {
        chromedriver.get(mainProperties.baseUrl());
        YandexMarket_PageObject ympo = new YandexMarket_PageObject(chromedriver);
        CookieHandler ck = new CookieHandler(chromedriver);
        ympo.pushCatalogueButton();
        ck.handleCookieBanner();
        ympo.selectMainPopUpElement(mainMenu);
        ympo.pushSecondaryMenuButton(additionalMenu);
        ympo.setManufacturersUniversal(m1);
        ympo.setPrices(min, max);
        Assertions.assertTrue(ympo.checkPrices(min,max), "Соответсвуют ли цены фильтру");
        Assertions.assertTrue(ympo.checkManufacturers(m1),"Соотвтетсвуют ли производители фильтру");
    }
}
