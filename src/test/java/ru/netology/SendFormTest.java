package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class SendFormTest {

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    String planningDate = generateDate(5);
    String invalidDate = generateDate(2);
    String city = "Санкт-Петербург";
    String invalidCity = "Приозерск";
    String name = "Петров-Водкин Иван";
    String invalidName = "Petrov Ivan";
    String phone = "+79876543210";
    String invalidPhone = "79876543210";


    @BeforeEach
    void openWebService() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSendForm() {
        $("[data-test-id=city] input").val(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").val(planningDate);
        $("[data-test-id=name] input").val(name);
        $("[data-test-id=phone] input").val(phone);
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content").shouldHave(text("Встреча успешно забронирована на " + planningDate))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldSendFormWithSuggest() {
        String planningDateNextWeek = generateDate(7);
        String planningDay = planningDateNextWeek.substring(0, 2);
        $("[data-test-id=city] input").val("Са");
        $(withText(city)).scrollTo().click();
        $("button .icon_name_calendar").click();
        $$("[data-day]").filterBy(visible).findBy(exactText(planningDay)).click();
        $("[data-test-id=name] input").val(name);
        $("[data-test-id=phone] input").val(phone);
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .shouldHave(text("Встреча успешно забронирована на " + planningDateNextWeek))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotSendFormForInvalidCity() {
        $("[data-test-id=city] input").val(invalidCity);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").val(planningDate);
        $("[data-test-id=name] input").val(name);
        $("[data-test-id=phone] input").val(phone);
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldBe(exactText("Доставка в выбранный город недоступна"));
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotSendFormForInvalidDate() {
        $("[data-test-id=city] input").val(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").val(invalidDate);
        $("[data-test-id=name] input").val(name);
        $("[data-test-id=phone] input").val(phone);
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=date] .input__sub")
                .shouldBe(exactText("Заказ на выбранную дату невозможен"));
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotSendFormForInvalidName() {
        $("[data-test-id=city] input").val(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").val(planningDate);
        $("[data-test-id=name] input").val(invalidName);
        $("[data-test-id=phone] input").val(phone);
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=name].input_invalid .input__sub")
                .shouldBe(exactText("Имя и Фамилия указаные неверно. " +
                        "Допустимы только русские буквы, пробелы и дефисы."));
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotSendFormForInvalidPhone() {
        $("[data-test-id=city] input").val(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").val(planningDate);
        $("[data-test-id=name] input").val(name);
        $("[data-test-id=phone] input").val(invalidPhone);
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldBe(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));

    }

    @Test
    void shouldNotSendFormForEmptyFields() {
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldBe(exactText("Поле обязательно для заполнения"));
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=city] input").val(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=date] .input__sub")
                .shouldBe(exactText("Неверно введена дата"));
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=date] input").val(planningDate);
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=name].input_invalid .input__sub")
                .shouldBe(exactText("Поле обязательно для заполнения"));
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=name] input").val(name);
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldBe(exactText("Поле обязательно для заполнения"));
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotSendFormForCheckBoxNotChecked() {
        $("[data-test-id=city] input").val(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").val(planningDate);
        $("[data-test-id=name] input").val(name);
        $("[data-test-id=phone] input").val(phone);
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=agreement].input_invalid")
                .shouldBe(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
        $(withText("Успешно!")).shouldNotBe(visible, Duration.ofSeconds(15));
    }
}