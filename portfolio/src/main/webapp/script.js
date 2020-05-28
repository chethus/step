// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact to the page.
 */
function randomFact() {
  const facts = [
      'I am a twin.',
      'My initials are the first three letters of the alphabet (CAB).',
      'I learned HTML, CSS, and Javascript to make this website.',
      'I enjoyed math and programming contests in high school.',
      'My astrological sign is Cancer.',
      'I have two brothers and no sisters.',
      'I was born 11 minutes before my twin brother.',
      'I attended 6 different schools from K-12.'
      ];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

//Shuffles an array, used for image gallery
function shuffle(arr) {
    //Number of unshuffled images at the front of the array
    var lenLeft = arr.length;
    for (var i = lenLeft; i >= 0; i --) {
        //Picks a random unshuffled element
        const ind = Math.floor(Math.random() * lenLeft);
        //Moves element to back of the array
        const ele = arr.splice(ind, 1)[0];
        arr.push(ele);
        lenLeft --;
    }
}

const galleryImages = [
    {
        name: "cern.jpg",
        caption: "My family at CERN"
    },
    {
        name: "deer.jpg",
        caption: "A deer looking through a window at my house"
    },
    {
        name: "kungfu.gif",
        caption: "My twin brother and I doing a kung fu form."
    },
    {
        name: "mountains.jpg",
        caption: "A picture of me at Mount Pilatus."
    },
    {
        name: "dollar.jpg",
        caption: "Me holding a Zimbabwean dollar I won in economics class."
    }
];

shuffle(galleryImages);

let HTMLImage;
let HTMLCaption;
let imageInd;

//Must wait until window loads so that html elements exist
window.onload = function setUpImage() {
    HTMLImage = document.getElementById("gallery");
    HTMLCaption = document.getElementById("caption");
    imageInd = galleryImages.length - 1;
    //Next image will be the image at index 0
    nextImage();
}

function nextImage() {
    imageInd = (imageInd + 1) % galleryImages.length;
    const imageName = galleryImages[imageInd].name;
    const imageCaption = galleryImages[imageInd].caption;
    HTMLImage.setAttribute("src", "gallery/" + imageName);
    //Wait until the image is loaded to change the caption
    HTMLImage.onload = function() {
        HTMLCaption.innerText = imageCaption;
    }
}

function prevImage() {
    //Moving two images back and one forward gets the previous image
    imageInd -= 2;
    if (imageInd < 0) {
        imageInd += galleryImages.length;
    }
    nextImage();
}