package ru.netology.testmode.test;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import ru.netology.testmode.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.testmode.data.DataGenerator.*;
import static ru.netology.testmode.data.DataGenerator.Registration.getRegisteredUser;
import static ru.netology.testmode.data.DataGenerator.Registration.getUser;

class AuthTest {
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        var registeredUser = getRegisteredUser("active");
        fillUserFormAndSubmit(registeredUser);
        checkSuccessResponse();
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        var notRegisteredUser = getUser("active");
        fillUserFormAndSubmit(notRegisteredUser);
        String expected = "Ошибка! Неверно указан логин или пароль";
        checkErrorResponseNotification(expected);
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = getRegisteredUser("blocked");
        fillUserFormAndSubmit(blockedUser);
        String expected = "Ошибка! Пользователь заблокирован";
        checkErrorResponseNotification(expected);
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = getRegisteredUser("active");
        var wrongLogin = getRandomLogin();
        fillUserFormAndSubmit(new RegistrationDto(
                wrongLogin,
                registeredUser.getPassword(),
                registeredUser.getStatus()
        ));
        String expected = "Ошибка! Неверно указан логин или пароль";
        checkErrorResponseNotification(expected);
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = getRegisteredUser("active");
        var wrongPassword = getRandomPassword();
        fillUserFormAndSubmit(new RegistrationDto(
                registeredUser.getLogin(),
                wrongPassword,
                registeredUser.getStatus()
        ));
        String expected = "Ошибка! Неверно указан логин или пароль";
        checkErrorResponseNotification(expected);
    }

    private void fillUserFormAndSubmit(RegistrationDto user) {
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(user.getLogin());
        form.$("[data-test-id=password] input").setValue(user.getPassword());
        form.$(By.className("button_theme_alfa-on-white")).click();
    }

    private void checkSuccessResponse(){
        $(By.className("heading_theme_alfa-on-white"))
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Личный кабинет"));
    }

    private void checkErrorResponseNotification(String expected){
        $("[data-test-id=error-notification] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldBe(text(expected));
    }
}
