package wtf.silhouette.shaeware.features.modules.combat;

import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;

public final class KillAuraModule extends AbstractModule {

  public KillAuraModule() {
    super("KillAura", "Automatically hits entities.", ModuleCategory.COMBAT);
  }
}
