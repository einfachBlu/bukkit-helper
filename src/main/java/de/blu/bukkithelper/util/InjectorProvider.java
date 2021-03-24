package de.blu.bukkithelper.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public final class InjectorProvider {

  private static Injector injector;

  @Inject
  private InjectorProvider(Injector injector) {
    InjectorProvider.injector = injector;
  }

  public static Injector get() {
    return InjectorProvider.injector;
  }
}
