package bot.ninetail.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A mutable pair class.
 */
@AllArgsConstructor
@NoArgsConstructor
public class MutablePair<T, U> {
    @Getter
    @Setter
    private T first;

    @Getter
    @Setter
    private U second;
}
