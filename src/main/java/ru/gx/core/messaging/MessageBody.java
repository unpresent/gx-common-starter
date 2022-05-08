package ru.gx.core.messaging;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface MessageBody {
    /**
     * @return Класс данных в body для однокомпонентных body. Если в body будет сложная структура, то null.
     */
    @Nullable
    Class<?> getDataClass();
}
