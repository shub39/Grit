package com.shub39.grit.web_demo.di

import com.shub39.grit.shared.ui.di.UIModules
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [UIModules::class])
@ComponentScan("com.shub39.grit.web_demo")
class AppModule