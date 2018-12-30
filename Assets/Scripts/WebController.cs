using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class WebController : MonoBehaviour {

    class AndroidPluginCallback : AndroidJavaProxy {
        public AndroidPluginCallback() : base("com.example.matthew.webViewPlugin.PluginCallback") { }
        public WebController webController;

        public void onFrameUpdate(AndroidJavaObject bytesObj) {
            Debug.Log("Callback onSuccess From Unity!");

            AndroidJavaObject bufferObject = bytesObj.Get<AndroidJavaObject>("Buffer");
            byte[] bytes = AndroidJNIHelper.ConvertFromJNIArray<byte[]>(bufferObject.GetRawObject());

            UnityThread.executeCoroutine(webController.LoadImageRoutine(bytes));
        }
    }

    AndroidJavaObject activity;
    AndroidJavaObject plugin;
    AndroidJavaObject data;

    bool shouldLoadMainScreen = true;

    private void Start() {
        UnityThread.initUnityThread();
        InitPlugin();
    }

    public void SetLoadMainScreen(bool shoulLoadMan) {
        shouldLoadMainScreen = shoulLoadMan;
    }

    void InitPlugin() {

        AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

        activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

        activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
            plugin = new AndroidJavaObject(
            "com.example.matthew.webViewPlugin.WebBridge");
        }));

        activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
            plugin.Call("init");
        }));

        //set callback
        AndroidPluginCallback androidPluginCallback = new AndroidPluginCallback {webController = this};
        activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
            plugin.Call("SetUnityBitmapCallback", androidPluginCallback);
        }));
    }

    IEnumerator LoadImageRoutine(byte[] bytes) {
        yield return new WaitForEndOfFrame();



    }

    public void GetImageFromURL(string url) {
        // Calls the function from the jar file
        activity.Call("runOnUiThread", new AndroidJavaRunnable(() => {
            plugin.Call("GetBitmap", url);
        }));
    }
}
