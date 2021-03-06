package example.com.capstoneproject.gui_layer;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by Arin on 23/05/16.
 */
public class DetailsTransition extends TransitionSet
{
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DetailsTransition()
    {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
