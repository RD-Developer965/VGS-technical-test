import { render, screen, fireEvent } from '@testing-library/react';


import { vi } from 'vitest';
import GameOverModal from '../../components/GameOverModal';
import { GameStatus } from '../../types/game';

// Mock useNavigate from react-router-dom
const mockedNavigate = vi.fn();

vi.mock('react-router-dom', () => ({
  useNavigate: () => mockedNavigate,
}));

describe('GameOverModal', () => {
  beforeEach(() => {
    mockedNavigate.mockClear(); // limpiar llamadas antes de cada test
  });

  it('renders nothing when isOpen is false', () => {
    const { container } = render(<GameOverModal isOpen={false} status={GameStatus.X_WON} />);
    expect(container).toBeEmptyDOMElement(); // no se renderiza nada
  });

  it('shows correct message when X wins', () => {
    render(<GameOverModal isOpen={true} status={GameStatus.X_WON} />);
    expect(screen.getByText('Player X Wins! 🎉')).toBeInTheDocument();
    expect(screen.getByText('Congratulations on your victory!')).toBeInTheDocument();
  });

  it('shows correct message when O wins', () => {
    render(<GameOverModal isOpen={true} status={GameStatus.O_WON} />);
    expect(screen.getByText('Player O Wins! 🎉')).toBeInTheDocument();
  });

  it('shows correct message when it is a draw', () => {
    render(<GameOverModal isOpen={true} status={GameStatus.DRAW} />);
    expect(screen.getByText("It's a Draw!")).toBeInTheDocument();
    expect(screen.getByText("Great game! Both players played excellently.")).toBeInTheDocument();
  });

  it('calls navigate("/") when Play Again button is clicked', () => {
    render(<GameOverModal isOpen={true} status={GameStatus.X_WON} />);
    const button = screen.getByText('Play Again');
    fireEvent.click(button);
    expect(mockedNavigate).toHaveBeenCalledWith('/');
  });
});

