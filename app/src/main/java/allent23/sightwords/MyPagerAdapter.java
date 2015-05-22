package allent23.sightwords;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter
{
    public int getCount() {
        return 4;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Using different layouts in the view pager instead of images.
        int resId = -1;
        View view = null;

        //Getting my layout's in my adapter. Three layouts defined.
        switch (position) {
            case 0:
                resId = R.layout.optionfrag1; //settings
                view = inflater.inflate(resId, container, false);
                ((ViewPager) container).addView(view, 0);
                break;

            case 1:
                resId = R.layout.optionfrag2; //flashcards
                view = inflater.inflate(resId, container, false);
                ((ViewPager) container).addView(view, 0);
                break;

            case 2:
                resId = R.layout.optionfrag4; //writing
                view = inflater.inflate(resId, container, false);
                ((ViewPager) container).addView(view, 0);
                break;

            case 3:
                resId = R.layout.optionfrag3; //quiz
                view = inflater.inflate(resId, container, false);
                ((ViewPager) container).addView(view, 0);
                break;
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
