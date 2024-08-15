package com.ocrf.bff.service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Messages {


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "{0}", MessageType.ERROR),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "У вас недостаточно прав, чтобы создавть или редактировать данный объект", MessageType.ERROR),
    NO_DATA(HttpStatus.UNPROCESSABLE_ENTITY, "Данные отсутсвуют в базе данных", MessageType.ERROR),//2
    IN_USE(HttpStatus.UNPROCESSABLE_ENTITY, "{0} \"{1}\" удалить нельзя. Используется в {2}", MessageType.ERROR),//5
    NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY,
            "Объект {0} c {1} = {2} не найден.",
            MessageType.ERROR), // 10
    NAME_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "Объект с таким {0} уже существует", MessageType.ERROR),
    INCORRECT_STATUS(HttpStatus.UNPROCESSABLE_ENTITY, "Неверный статус {0}", MessageType.ERROR),
    INCORRECT_FIELD(HttpStatus.UNPROCESSABLE_ENTITY, "Неправильное значение поля: {0}. Возможные значения: {1}", MessageType.ERROR),
    MANDATORY_FIELD_MISSING(HttpStatus.UNPROCESSABLE_ENTITY, "Отсутствует обязательное поле: {0}", MessageType.ERROR),
    DELETE_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "Невозможно удалить {0}. Объект содержит записи", MessageType.ERROR),
    INCORRECT_LENGTH(HttpStatus.UNPROCESSABLE_ENTITY, "Допустимая длина ввода в поле \"{0}\": {1}", MessageType.ERROR),//11
    INCORRECT_RELEASE_YEAR(HttpStatus.UNPROCESSABLE_ENTITY, "Год не должен быть меньше 1990 и больше {0}", MessageType.ERROR), // 12
    INCORRECT_ISSUE_DATE(HttpStatus.UNPROCESSABLE_ENTITY, "Дата утверждения {0} не соответствует году издания {1}", MessageType.ERROR), // 13
    INCORRECT_RANGE(HttpStatus.UNPROCESSABLE_ENTITY, "Значение в поле \"{0}\" должно быть > {1} и < {2}", MessageType.ERROR), // 15
    INCORRECT_RANGE_EQUALS(HttpStatus.UNPROCESSABLE_ENTITY, "Значение в поле \"{0}\" должно быть >= {1} и =< {2}", MessageType.ERROR), // 15
    INCORRECT_TIME_SUM(HttpStatus.UNPROCESSABLE_ENTITY, "Сумма значений Тпз, Тоб,Тпт, Тотл должна быть меньше 100%", MessageType.ERROR), // 16
    TANIMOTO_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "Есть похожее имя: {0}", MessageType.ERROR),
    INCORRECT_TEAM(HttpStatus.UNPROCESSABLE_ENTITY, "Заполните основной состав для раздела \"Исполнители\"", MessageType.ERROR), // 20
    INCORRECT_TEAM_CONTENT(HttpStatus.UNPROCESSABLE_ENTITY, "Исполнители должны быть из состава, указанного на норме времени к работе", MessageType.ERROR), // 23
    INCORRECT_TEAM_QUANTITY(HttpStatus.UNPROCESSABLE_ENTITY, "Количество исполнителей альтернативного состава должно быть таким же как у основного", MessageType.ERROR), // 22
    HAS_CHILDREN(HttpStatus.UNPROCESSABLE_ENTITY, "Значение в поле \"{0}\" {1} нельзя, есть дочерние записи", MessageType.ERROR),
    RESOURCE_ALREADY_LOCKED(HttpStatus.UNPROCESSABLE_ENTITY, "Ресурс уже заблокирован {0}", MessageType.WARNING);

    private HttpStatus httpStatus;

    private String textPattern;

    MessageType messageType;
}
