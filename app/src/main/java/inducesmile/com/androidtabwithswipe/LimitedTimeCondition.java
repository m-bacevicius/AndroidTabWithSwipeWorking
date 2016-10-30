package inducesmile.com.androidtabwithswipe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cube on 10/27/2016.
 */

public class LimitedTimeCondition
{
    private CountDownLatch conditionMetLatch;
    private Integer unitsCount;
    private TimeUnit unit;

    public LimitedTimeCondition(final Integer unitsCount, final TimeUnit unit)
    {
        conditionMetLatch = new CountDownLatch(1);
        this.unitsCount = unitsCount;
        this.unit = unit;
    }

    public boolean waitForConditionToBeMet()
    {
        try
        {
            return conditionMetLatch.await(unitsCount, unit);
        }
        catch (final InterruptedException e)
        {
            System.out.println("Someone has disturbed the condition awaiter.");
            return false;
        }

    }

    public void conditionWasMet()
    {
        conditionMetLatch.countDown();
    }
}