package wtf.silhouette.shaeware.utils.player;

import net.minecraft.util.PlayerInput;
import org.jetbrains.annotations.NotNull;

public record DirectionalInput(boolean forwards, boolean backwards, boolean left, boolean right) {

    public DirectionalInput(@NotNull PlayerInput input) {
        this(input.forward(), input.backward(), input.left(), input.right());
    }

    public DirectionalInput(float movementForward, float movementSideways) {
        this(movementForward > 0.0F, movementForward < 0.0F, movementSideways > 0.0F, movementSideways < 0.0F);
    }
}
