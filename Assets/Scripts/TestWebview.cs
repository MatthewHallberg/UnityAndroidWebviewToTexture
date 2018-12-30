using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof (WebController))]
public class TestWebview : MonoBehaviour {

    public Renderer render;
    WebController webPlugin;

    // Start is called before the first frame update
    void Start() {
        webPlugin = GetComponent<WebController>();
    }

    private void OnEnable() {
        WebController.resultRecieved += GotResult;
    }

    private void OnDisable() {
        WebController.resultRecieved -= GotResult;
    }

    private void GotResult(byte[] bytes) {
        Texture2D tex = new Texture2D(2, 2);
        tex.LoadImage(bytes);
        render.material.mainTexture = tex;
    }

    public void LoadWebsite(string site) {
        string url = "http://" + site + ".com";
        webPlugin.GetImageFromURL(url);
    }
}
