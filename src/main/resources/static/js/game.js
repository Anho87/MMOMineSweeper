const boardSize = 100;
const socket = new SockJS('/game');
const stompClient = Stomp.over(socket);
const gameBoard = document.getElementById("game-board");

function handleCellClick(event) {
    const cell = event.target;
    const index = cell.dataset.index;
    console.log("Cell clicked:", index);
    sendMove(index % boardSize, Math.floor(index / boardSize));
}

function sendMove(x, y) {
    stompClient.send("/app/move", {}, JSON.stringify({x: x, y: y}));
}

stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);

    stompClient.send("/app/connect", {}, {});

    const newGameStateSubscription = stompClient.subscribe('/topic/new-game-state', function (message) {
        const result = JSON.parse(message.body);
        console.log("Received current game state:", result);
        resetClientBoard();
        createBoard();
        renderBoardState(result);
        newGameStateSubscription.unsubscribe();
    });

    stompClient.subscribe('/topic/game-updates', function (message) {
        const result = JSON.parse(message.body);
        console.log("Game update:", result);
        updateBoard(result);
    });
}, function (error) {
    console.error('WebSocket connection error:', error);
});


function disableBoard() {
    const cells = document.querySelectorAll(".cell");
    cells.forEach(cell => {
        cell.removeEventListener("click", handleCellClick); 
        cell.classList.add("disabled"); 
    });
}

function renderBoardState(currentGameState) {
    currentGameState.forEach(cellUpdate => {
        updateSingleCell(cellUpdate);
    });
}

function resetClientBoard() {
    while (gameBoard.firstChild) {
        gameBoard.removeChild(gameBoard.firstChild);
    }
    const div = document.getElementById("resultDiv");
    div.innerText = "";
}

function updateBoard(result) {
    console.log("Updating board with result:", result);

    if (Array.isArray(result.revealedCells)) {
        console.log("Revealed cells:", result.revealedCells);
        result.revealedCells.forEach(cellUpdate => {
            updateSingleCell(cellUpdate);
        });
    } else {
        updateSingleCell(result);
    }

  
    if (result.win) {
        const div = document.getElementById("resultDiv");
        div.innerText = "You won!";
        disableBoard(); 
        setTimeout(function () {
            resetGame();
        }, 5000);
    }
}

function updateSingleCell(cellUpdate) {
    const cell = gameBoard.children[cellUpdate.cellIndex];

    if (!cell) {
        console.error(`Invalid cell index: ${cellUpdate.cellIndex}`);
        return;
    }

    if (cellUpdate.mine) {
        cell.classList.add('mine');
        cell.innerText = '💣';
        const div = document.getElementById("resultDiv");
        div.innerText = "You lost!";
        disableBoard(); 
        setTimeout(function () {
            resetGame();
        }, 5000);
    } else {
        if (cellUpdate.adjacentMines > 0) {
            cell.innerText = cellUpdate.adjacentMines;
        }
        cell.classList.add('revealed');
    }
}

function resetGame() {
    while (gameBoard.firstChild) {
        gameBoard.removeChild(gameBoard.firstChild);
    }

    const div = document.getElementById("resultDiv");
    div.innerText = "";
    fetch("/reset", {
        method: "POST"
    })
        .then(response => {
            if (response.ok) {
                console.log("Game has been reset by server.");
                createBoard();
            }
        })
        .catch(error => console.error("Fel vid återställning av spelet:", error));
}

createBoard();

function createBoard() {
    for (let i = 0; i < boardSize * boardSize; i++) {
        const cell = document.createElement("div");
        cell.className = "cell";
        cell.dataset.index = i;
        cell.addEventListener("click", handleCellClick);
        gameBoard.appendChild(cell);
    }
}
