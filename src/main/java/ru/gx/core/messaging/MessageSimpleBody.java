package ru.gx.core.messaging;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface MessageSimpleBody extends MessageBody {

    /**
     * @return Объект данных, содержащийся в теле сообщения
     */
    Object getData();

    /**
     * @return Класс данных в body для однокомпонентных body
     */
    @Nullable
    Class<?> getDataClass();
}
