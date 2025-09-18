export type Player = 'X' | 'O';

export enum CellValue {
  X = 'X',
  O = 'O',
  EMPTY = 'EMPTY'
}

export enum GameStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  X_WON = 'X_WON',
  O_WON = 'O_WON',
  DRAW = 'DRAW'
}

export interface Cell {
  row: number;
  column: number;
  value: CellValue;
}

export type Winner = Player | 'DRAW' | null;

export interface Game {
  id: number;
  createdAt: string;
  status: GameStatus;
  currentTurn: Player;
  board: Cell[];
}

export interface Square {
  x: number;
  y: number;
}

export interface MoveRequest {
  matchId: number;
  playerId: Player;
  square: Square;
}