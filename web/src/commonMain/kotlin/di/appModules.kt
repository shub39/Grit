package di

import data.DummyStateProvider
import domain.StateProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModules = module {
    singleOf(::DummyStateProvider).bind<StateProvider>()
}