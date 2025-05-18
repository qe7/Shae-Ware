package wtf.silhouette.shaeware.features.modules.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum ModuleCategory {
  COMBAT("Combat", new Color(255, 50, 50).getRGB()),
  EXPLOIT("Exploit", new Color(200, 50, 255).getRGB()),
  MISC("Misc", new Color(100, 255, 100).getRGB()),
  MOVEMENT("Movement", new Color(50, 150, 255).getRGB()),
  VISUAL("Visual", new Color(255, 150, 50).getRGB());

  private final String name;
  private final int color;
}
