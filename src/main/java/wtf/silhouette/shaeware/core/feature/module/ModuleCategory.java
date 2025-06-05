package wtf.silhouette.shaeware.core.feature.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum ModuleCategory {
  COMBAT("Combat", new Color(220, 40, 40).getRGB()),
  EXPLOIT("Exploit", new Color(170, 60, 220).getRGB()),
  MISC("Misc", new Color(80, 200, 120).getRGB()),
  MOVEMENT("Movement", new Color(40, 120, 220).getRGB()),
  VISUAL("Visual", new Color(255, 180, 70).getRGB()),
  GHOST("Ghost", new Color(180, 180, 255).getRGB()),
  PLAYER("Player", new Color(255, 220, 80).getRGB()),
  WORLD("World", new Color(120, 80, 200).getRGB()),
  CLIENT("Client", new Color(100, 100, 100).getRGB());

  private final String name;
  private final int color;
}
