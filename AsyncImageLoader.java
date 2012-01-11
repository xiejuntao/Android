/* Name: Yannru Jason cheng
* Description: An image loader mainly use for loading pictures
* in a list. To save memory and increase performance, softreference
* and imageCache is used
*/

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncImageLoader {
	private HashMap<String, SoftReference<Drawable>> imageCache;

	public AsyncImageLoader(){
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}
	public void fetchDrawableOnThread(final String imageUrl, final ImageView imageView){
		if(imageCache.containsKey(imageUrl)){
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if(drawable != null){
				if(imageView != null){
				imageView.setImageDrawable(drawable);
				return;
				}
			}
		}
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(imageView != null){
					imageView.setImageDrawable((Drawable) msg.obj);
				}
			}
		};
		new Thread(){
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				if(drawable != null){
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			} 
		}.start();

	}

	private Drawable loadImageFromUrl(String url){
		InputStream in = null;
		try {
			in = new URL(url).openStream();

		} catch (MalformedURLException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		return Drawable.createFromStream(in, "src"); 
	}
}

