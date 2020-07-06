package lightricks.yaakov.bally.physics;

import com.google.auto.value.AutoValue;

/**
 * Hold data of the tracked object and it container
 */
@AutoValue
public abstract class TrackedObject {
    public static TrackedObject create(int containerWidth, int containerHeight, int objectWidth, int objectHeight){
        if (containerWidth <= 0 || containerHeight <= 0 || objectWidth <= 0 || objectHeight <= 0){
            String ex = "all argument should follow: parameter > 0\tactual\n:containerWidth=%s\n:containerHeight=%s\n:objectWidth=%s\n:objectHeight=%s";
            throw new IllegalArgumentException(String.format(ex, containerWidth, containerHeight, objectWidth, objectHeight));
        }
        return new AutoValue_TrackedObject(containerWidth, containerHeight, objectWidth, objectHeight);
    }

    public abstract int containerWidth();
    public abstract int containerHeight();
    public abstract int objectWidth();
    public abstract int objectHeight();
}
