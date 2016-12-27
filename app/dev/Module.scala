package dev

import javax.inject.Singleton

import com.google.inject.AbstractModule

/**
  * Created by Klaus on 23.12.2016.
  */
@Singleton
class Module extends AbstractModule {

    override def configure(): Unit = {
        bind[TestdataInjector](classOf[TestdataInjector]).asEagerSingleton()
    }

}
