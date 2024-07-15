
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendAdminNotification = functions.database.ref('/chat/{pushId}').onWrite((change, context) => {
    // var topic = 'News';
    // console.log("referencia",functions.database.ref('/News/{pushId}'))
    console.log("3")
    const news = change.after.val();
    console.log("4")
    console.log("new_", news)
    const payload = {
        notification: {
            title: `Spook chat`,
            body: `${news.nombre}` + " escribió: " + `${news.mensaje}`,

        }
    };
    console.log("payload", payload)
    console.log("4")
    console.log("Valdemar Historia")

    return admin.messaging().sendToTopic("chat", payload)
        .then(function (response) {
            console.log('Notification sent successfully:', response);
        })
        .catch(function (error) {
            console.log('Notification sent failed:', error);
        });
});


exports.sendNewHistoryNotification = functions.database.ref('/Historias/{newId}')
    .onCreate((snapshot, context) => {
        const data = snapshot.val()
        const { author, category, desc, image, title } = data

        const authorNameCapitalized = author.charAt(0).toUpperCase() + author.slice(1)
        const notification = {
            title: `${authorNameCapitalized} ha agregado una historia.`,
            body: `Dale un vistazo a ${title}`,
            sound: 'default'
        }

        if (!!image && (image.startsWith("http"))) {
            notification["icon"] = image
            notification["image"] = image
        }

        const payload = {
            notification,
            data: {
                history: JSON.stringify(data)
            }
        }

        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24 //24 hours
        }

        return admin.messaging().sendToTopic("histories_notifications", payload, options)
            .then((response) => {
                console.log(response)
            }).catch(err => {
                console.log(err)
            })
    })


    exports.sendNewHistoryNotification = functions.database.ref('/chats/{newId}')
    .onCreate((snapshot, context) => {
        const data = snapshot.val()
        const { author, imagen, titulo } = data

        const authorNameCapitalized = author.charAt(0).toUpperCase() + author.slice(1)
        const notification = {
            title: `${authorNameCapitalized} ha agregado una historia.`,
            body: `Dale un vistazo a ${titulo}`,
            sound: 'default'
        }

        if (!!imagen && (imagen.startsWith("http"))) {
            notification["icon"] = imagen
            notification["image"] = imagen
        }

        const payload = {
            notification,
            data: {
                history: JSON.stringify(data)
            }
        }

        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24 //24 hours
        }

        return admin.messaging().sendToTopic("chats_notifications", payload, options)
            .then((response) => {
                console.log(response)
            }).catch(err => {
                console.log(err)
            })
    })


// exports.sendAdminNotification = functions.database.ref('/Historias/{pushId}')
// .onWrite((change, context) => {
//     const news = change.after.val();
//     const payload = {
//         notification: {
//             title:  `${news.title}`,
//             body: `${news.mensaje}`,
//         }
//     }; 

//     return admin.messaging().sendToTopic("Historias", payload)
//         .then(function (response) {
//             console.log('Notification sent successfully:', response);
//         })
//         .catch(function (error) {
//             console.log('Notification sent failed:', error);
//         });
// });