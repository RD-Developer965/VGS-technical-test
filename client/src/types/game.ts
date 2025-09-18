export type Player = 'X' | 'O';
export type CellValue = Player | null;
export type Board = CellValue[][];

export interface Game {
  id: number;
  board: Board;
  currentPlayer: Player;
  winner: Player | 'DRAW' | null;
  isFinished: boolean;
}

export interface MoveRequest {
  matchId: number;
  playerId: Player;
  square: {
    x: number;
    y: number;
  };
}

export interface GameResponse {
  id: number;
  board: Board;
  currentPlayer: Player;
  winner: Player | 'DRAW' | null;
  finished: boolean;
}