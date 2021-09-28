package ru.gx.common.updatable;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Общая информация об обновляемом свойстве.
 *
 * @author Adolin Negash 23.05.2021
 */
class UpdatablePropertyInfo {

    /**
     * Значение свойства.
     */
    @Getter
    private String value;

    /**
     * Список элементов бинов (поля и сеттеры), которые привязаны к этому свойству.
     */
    private final List<UpdatableBeanMemberInfo> members = new ArrayList<>();

    /**
     * Создает объект с информацией об обновляемых свойствах.
     *
     * @param value начальное значение свойства.
     */
    UpdatablePropertyInfo(String value) {
        this.value = value;
    }

    /**
     * Добавляет элемент бина.
     *
     * @param memberInfo элемент бина.
     */
    void addMember(UpdatableBeanMemberInfo memberInfo) {
        members.add(memberInfo);
    }

    /**
     * Проставляет значение свойства.
     *
     * @param value значение свойства.
     */
    void setValue(String value) {

        this.value = value;

        // final List<String> beans = new ArrayList<>();
        for (UpdatableBeanMemberInfo member : members) {
            member.setValue(value);
            // beans.add(member.getBeanName());
        }
    }

    /**
     * Возвращает список имен бинов, к которым привязано свойство.
     */
    List<String> getBeanNames() {
        return members.stream()
                .map(UpdatableBeanMemberInfo::getBeanName)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает true, если список элементов бинов пуст. Иначе - false.
     */
    boolean isEmpty() {
        return members.isEmpty();
    }
}
