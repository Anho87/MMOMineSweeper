// src/main/resources/static/js/game.js

const boardSize = 10; // Storleken på brädet (10x10)
const socket = new SockJS('/game'); // Anslut till WebSocket-endpoint
const stompClient = Stomp.over(socket);
const gameBoard = document.getElementById("game-board");

// Skapa spelbrädet
function createBoard() {
    for (let i = 0; i < boardSize * boardSize; i++) {
        const cell = document.createElement("div");
        cell.className = "cell";
        cell.dataset.index = i; // Spara cellens index
        cell.addEventListener("click", handleCellClick); // Lägg till eventlyssnare
        gameBoard.appendChild(cell);
    }
}

// Hantera klick på en cell
function handleCellClick(event) {
    const cell = event.target;
    const index = cell.dataset.index;
    console.log("Cell clicked:", index); // Lägg till detta för att se om funktionen anropas
    // Skicka spelarens drag till servern
    sendMove(index % boardSize, Math.floor(index / boardSize));
}

// Skicka spelarens drag
function sendMove(x, y) {
    stompClient.send("/app/move", {}, JSON.stringify({ x: x, y: y }));
}

// Anslut till WebSocket-servern
stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);

    // Prenumerera på speluppdateringar
    stompClient.subscribe('/topic/game-updates', function (message) {
        const result = JSON.parse(message.body);
        console.log("Game update:", result);

        // Här kan du uppdatera brädet baserat på spelets logik
        updateBoard(result);
    });
}, function (error) {
    console.error('WebSocket connection error:', error);
});



// Funktion för att uppdatera spelbrädet
function updateBoard(result) {
    console.log("Updating board with result:", result); // Logga hela resultatet

    // Kontrollera om vi fått en lista av avslöjade celler
    if (Array.isArray(result.revealedCells)) {
        console.log("Revealed cells:", result.revealedCells);
        // Gå igenom varje avslöjad cell
        result.revealedCells.forEach(cellUpdate => {
            updateSingleCell(cellUpdate);
        });
    } else {
        // Hantera ett enskilt GameUpdate-objekt (t.ex. en mina träffad)
        updateSingleCell(result);
    }

    // Om spelet har vunnit, visa vinstmeddelande
    if (result.isWin) {
        alert('Du har vunnit!');
    }
}

function updateSingleCell(cellUpdate) {
    const cell = gameBoard.children[cellUpdate.cellIndex];
    console.log("Hej1");

    // Kolla om cellen är en mina
    if (cellUpdate.mine) {
        console.log("Hej2");
        cell.classList.add('mine'); // Om cellen är en mina
        cell.innerText = '💣'; // Visa en mina
    } else {
        // Visa antalet angränsande miner om det finns
        if (cellUpdate.adjacentMines > 0) {
            cell.innerText = cellUpdate.adjacentMines; // Visa angränsande miner
        }
        console.log("Hej3");
        cell.classList.add('revealed'); // Markera cellen som avslöjad
    }
}





// Funktion för att återställa spelet
function resetGame() {
    // Rensa brädet
    while (gameBoard.firstChild) {
        gameBoard.removeChild(gameBoard.firstChild);
    }

    // Skapa ett nytt spelbräde
    createBoard();

    // Skicka en begäran till servern för att återställa spelet
    fetch("/reset", {
        method: "POST"
    })
        .then(response => {
            if (response.ok) {
                console.log("Spelet har återställts på servern.");
            }
        })
        .catch(error => console.error("Fel vid återställning av spelet:", error));
}

// Initiera spelet
createBoard(); // Skapa spelbrädet

// Lägg till en eventlyssnare för återställningsknappen
document.getElementById("reset-button").addEventListener("click", function() {
    resetGame(); // Återställ spelet
});
