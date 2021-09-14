package Model.Grid;

/**
 * Enumeration allowing to identify cell's nature
 * @author Maia
 *
 */
public enum GameCases {
	
	EMPTY_CASE {
		@Override
		public String toString() {
			return "*";
		}
	},
	BATTLESHIP_CASE {
		@Override
		public String toString() {
			return "B";
		}
	},
	CRUISER_CASE {
		@Override
		public String toString() {
			return "C";
		}
	},
	DESTROYER_CASE {
		@Override
		public String toString() {
			return "D";
		}
	},
	SUBMARINE_CASE {
		@Override
		public String toString() {
			return "S";
		}
	},
	WRECKAGE_CASE {
		@Override
		public String toString() {
			return "X";
		}
	}

}
