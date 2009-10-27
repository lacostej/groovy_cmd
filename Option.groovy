import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
   String description()
}
