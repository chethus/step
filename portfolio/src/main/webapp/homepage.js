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