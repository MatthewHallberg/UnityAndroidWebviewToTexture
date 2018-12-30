using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof (WebController))]
public class TestWebview : MonoBehaviour {

    WebController webPlugin;

    // Start is called before the first frame update
    void Start() {
        webPlugin = GetComponent<WebController>();
    }

    // Update is called once per frame
    void Update() {

    }
}
