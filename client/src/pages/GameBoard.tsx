import { useParams } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';
import { type Game, type MoveRequest, type Cell, GameStatus, CellValue } from '../types/game';
import axios from 'axios';
import { useGameStore } from '../store/gameStore';
import { GameOverModal } from '../components/GameOverModal';

const API_URL = 'http://localhost:8080/api/games';

async function makeMove(move: MoveRequest) {
  const response = await axios.post<Game>(`${API_URL}/move`, move);
  return response.data;
}

async function getGameStatus(gameId: number) {
  const response = await axios.get<Game>(`${API_URL}/status?matchId=${gameId}`);
  return response.data;
}

// Función auxiliar para convertir el array plano en una matriz 3x3
function transformBoard(board: Cell[]): (Cell | null)[][] {
  const matrix: (Cell | null)[][] = Array(3).fill(null).map(() => Array(3).fill(null));
  
  board.forEach(cell => {
    matrix[cell.row - 1][cell.column - 1] = cell;
  });
  
  return matrix;
}

function GameBoard() {
  const { gameId } = useParams();
  const { currentPlayer, setCurrentPlayer } = useGameStore();
  const { data: gameStatus, refetch: refetchStatus } = useQuery({
    queryKey: ['gameStatus', gameId],
    queryFn: async () => {
      if (!gameId) return null;
      const data = await getGameStatus(Number(gameId));
      // Actualizamos el turno actual en el store
      setCurrentPlayer(data.currentTurn);
      return data;
    },
    enabled: !!gameId
  });

  const makeMoveMutation = useMutation({
    mutationFn: makeMove,
    onSuccess: () => {
      refetchStatus();
    },
    onError: (error) => {
      if (axios.isAxiosError(error)) {
        toast.error(error.response?.data?.message || 'Error making move');
      } else {
        toast.error('Something went wrong');
      }
    }
  });

  const handleCellClick = (x: number, y: number) => {
    if (!gameId || !gameStatus) return;

    makeMoveMutation.mutate({
      matchId: Number(gameId),
      playerId: currentPlayer,
      square: { x: x + 1, y: y + 1 },
    });
  };

  if (!gameStatus) {
    return (
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
        <p className="mt-4 text-gray-600">Loading game...</p>
      </div>
    );
  }

  return (
    <div className="bg-white p-10 rounded-xl shadow-2xl backdrop-blur-sm bg-white/90 max-w-lg mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800 tracking-tight">Tic Tac Toe</h1>
      </div>
      
      <div className="grid grid-cols-3 gap-4 mb-8">
        {transformBoard(gameStatus.board).map((row, i) =>
          row.map((cell, j) => (
            <button
              key={`${i}-${j}`}
              onClick={() => handleCellClick(i, j)}
              disabled={cell?.value !== CellValue.EMPTY || gameStatus.status !== GameStatus.IN_PROGRESS}
              className={` bg-gray-800 hover:bg-gray-700 appearance-none border-0 text-[2.5rem] leading-none w-24 h-24 font-bold flex items-center justify-center rounded-xl transform transition-all duration-200 
                focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50 disabled:cursor-not-allowed
                ${cell?.value === CellValue.EMPTY && gameStatus.status !== GameStatus.IN_PROGRESS ? 'hover:scale-105 hover:shadow-lg ' : ''}
                ${cell?.value === 'X' ? 'text-white' : 
                  cell?.value === 'O' ? 'text-white' : 
                  ''}`}
            >
              {cell?.value === CellValue.EMPTY ? '' : cell?.value}
            </button>
          ))
        )}
      </div>

      <div className="bg-gray-50 rounded-lg p-4">
        <div className="text-center">
          {gameStatus.status === 'IN_PROGRESS' && (
            <div className="flex items-center justify-center space-x-2">
              <div className={`w-3 h-3 rounded-full ${gameStatus.currentTurn === 'X' ? 'bg-primary-500' : 'bg-secondary-500'} animate-pulse`}></div>
              <span className="text-gray-700 font-medium">
                Current turn: {gameStatus.currentTurn}
              </span>
            </div>
          )}
        </div>
      </div>

      <GameOverModal
        isOpen={gameStatus.status !== GameStatus.IN_PROGRESS}
        winner={gameStatus.currentTurn}
      />
    </div>
  );
}

export default GameBoard;