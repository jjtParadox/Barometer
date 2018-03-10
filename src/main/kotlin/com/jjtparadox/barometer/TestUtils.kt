/*
 * This file is part of Barometer
 *
 * Copyright (c) 2018 jjtParadox
 *
 * Barometer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Barometer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Barometer. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jjtparadox.barometer

object TestUtils {
    @JvmStatic fun tickServer() {
        //TODO tick the server in a way that doesn't autosave unless requested
        Barometer.server.tick()
    }
}
