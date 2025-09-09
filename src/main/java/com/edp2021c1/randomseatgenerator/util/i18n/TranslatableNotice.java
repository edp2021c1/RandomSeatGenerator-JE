/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.util.i18n;

import com.edp2021c1.randomseatgenerator.util.Notice;

@FunctionalInterface
public interface TranslatableNotice extends Notice {

    String TR_NOTICE = I18N.ROOT_KEY + "notice.";

    static TranslatableNotice of(String message, Object... messageArgs) {
        return new TranslatableNotice() {
            @Override
            public String messageKey() {
                return TR_NOTICE + message;
            }

            @Override
            public Object[] messageArgs() {
                return messageArgs;
            }
        };
    }

    default String titleKey() {
        return I18N.ROOT_KEY + "notice";
    }

    default Object[] titleArgs() {
        return new Object[0];
    }

    String messageKey();

    default Object[] messageArgs() {
        return new Object[0];
    }

    @Override
    default String title() {
        return I18N.tr(titleKey(), titleArgs());
    }

    @Override
    default String message() {
        return I18N.tr(messageKey(), messageArgs());
    }

}
