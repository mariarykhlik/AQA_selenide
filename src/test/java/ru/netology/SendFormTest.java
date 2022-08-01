package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class SendFormTest {
    @BeforeEach
    void openWebService() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSendForm() {
        $("[data-test-id=city] input").val("Санкт-Петербург");
        Date date = new Date();
        String testDate = date.toString();
        $("[data-test-id=date] input").val(testDate);
        $("[data-test-id=name] input").val("Петров-Водкин Иван");
        $("[data-test-id=phone] input").val("+79876543210");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
    }
}