import styles from "./TeamSelection.module.css";

import WKingImg from "../assets/white-king.png";
import BKingImg from "../assets/black-king.png";
import { Team } from "../App";

interface Props {
  onSelection(s: Team): void;
}

export const TeamSelection = ({ onSelection }: Props) => {
  return (
    <div className={styles.menu}>
      <div className={styles.cell}>
        <img src={WKingImg} onClick={() => onSelection("WHITE")} />
      </div>
      <div className={styles.cell}>
        <img src={BKingImg} onClick={() => onSelection("BLACK")} />
      </div>
    </div>
  );
};

export default TeamSelection;
