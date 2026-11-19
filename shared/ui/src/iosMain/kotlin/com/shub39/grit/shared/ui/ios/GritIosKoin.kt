/*
 * Copyright (C) 2026  Shubham Gorai
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
package com.shub39.grit.shared.ui.ios

import com.shub39.grit.logic.ios.IosModules
import com.shub39.grit.shared.ui.di.UIModules
import org.koin.core.annotation.KoinApplication

/**
 * The whole graph the iOS app runs on.
 *
 * `@KoinApplication(modules = …)` is deliberate: `@Module(includes = …)` pointing at a module that
 * lives in another Gradle module makes the Koin compiler plugin emit hint declarations for that
 * module's definitions into *both* klibs, and Kotlin/Native then rejects the framework link with
 * `IrSimpleFunctionSymbolImpl is already bound`. Listing the modules here keeps one hint set per
 * module, so both `:shared:logic` and `:shared:ui` can apply the compiler plugin and keep
 * compile-time graph safety.
 */
@KoinApplication(modules = [IosModules::class, UIModules::class]) object GritIosKoin
