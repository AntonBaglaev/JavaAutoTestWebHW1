# Семинар: Введение в Selenium WebDriver

## Задание 1.
Условие:
Написать ещё один тест на том же стенде (https://test-stand.gb.ru/login). Суть теста простая:
- логинимся под своими логином и паролем
- нажимаем на ‘+’ для добавления группы
- вводим имя новой группы
- нажимаем кнопку SAVE
- проверяем, что группа с именем появилась в таблице
- достаточно проверить что появился нужный title
- закрывать модальное окно создания группы не обязательно, таблица и так успешно прочитается

Требования и рекомендации:
требуется корректно использовать явные ожидания: после логина, в момент открытия модального окна
(оно может не успеть), после сохранения группы нужно дождаться появления искомого элемента предлагаем
в конце теста написать сохранение скриншота окна браузера, достаточно сохранять просто в директорию resources.
Использовать в задании корректную структуру тестового класса, как минимум BeforeAll, BeforeEach,
AfterEach методы для создания, настройки и закрытия драйвера

=======================================================================================================
## Test-Run проекта:
![](testrun.jpg)