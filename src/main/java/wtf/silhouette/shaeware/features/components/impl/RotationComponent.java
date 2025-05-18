package wtf.silhouette.shaeware.features.components.impl;

import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.features.components.api.Component;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;

public final class RotationComponent extends Component {

  @Getter
  private float[] currentRotation;

  @Setter
  private float[] nextRotation;

  @Getter
  private boolean rotating;

  public RotationComponent() {
    ManagerFactory.get(EventManager.class).registerHandler(this);
  }
}
