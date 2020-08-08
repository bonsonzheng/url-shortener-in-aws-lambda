function post_url() {

    let long_url = document.getElementById("url").value
    let result = document.getElementById("result")
    let url = "https://fs8k95s4t3.execute-api.ap-northeast-2.amazonaws.com/v1/url-shorten";

    var data = JSON.stringify({"longUrl": long_url});


    fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: data
    })
        .then(
            response => response.json()
        ).then(
        responseBody => result.innerHTML = '<a href = "' + url + '/' + responseBody["shortUrl"] + '" + >' + url + '/' + responseBody["shortUrl"] + '</a>'
    );
}