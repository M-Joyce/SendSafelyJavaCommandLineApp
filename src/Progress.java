import com.sendsafely.ProgressInterface;

import java.text.DecimalFormat;

public class Progress implements ProgressInterface //implementation to provide download progress.
{
    @Override
    public void updateProgress(String s, double v)
    {
        DecimalFormat df = new DecimalFormat("#%"); //making the percentage displayed a little more user-friendly.
        System.out.println("Uploading: " + s + " " + df.format(v));
    }

    @Override
    public void gotFileId(String s)
    {
        System.out.println("Starting upload on file with id: " + s);
    }
}
