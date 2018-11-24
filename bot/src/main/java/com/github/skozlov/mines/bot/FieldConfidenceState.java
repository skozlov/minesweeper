package com.github.skozlov.mines.bot;

import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.core.Cell;
import com.github.skozlov.mines.core.playerPov.CellPlayerPov;
import com.github.skozlov.mines.core.playerPov.FieldPlayerPov;

import java.util.*;
import java.util.stream.Collectors;

public final class FieldConfidenceState {
	private final Matrix<CellConfidenceState> cells;
	private final FieldPlayerPov source;

	public FieldConfidenceState(FieldPlayerPov field) {
		this(
			update(
				Matrix.create(
					field.getCells().getDimension(),
					c -> CellConfidenceState.Unknown.INSTANCE,
					CellConfidenceState.class
				),
				FieldPlayerPov.allIntact(field.getParameters()),
				field
			),
			field
		);
	}

	private FieldConfidenceState(Matrix<CellConfidenceState> cells, FieldPlayerPov source) {
		this.cells = cells;
		this.source = source;
	}

	public Matrix<CellConfidenceState> getCells() {
		return cells;
	}

	public FieldConfidenceState update(FieldPlayerPov field) {
		if (!field.getParameters().equals(source.getParameters())){
			throw new IllegalArgumentException(String.format(
				"The old field has parameters `%s`, but the new one - `%s`",
				source.getParameters(), field.getParameters()
			));
		}
		return new FieldConfidenceState(update(cells, source, field), field);
	}

	private static Matrix<CellConfidenceState> update(
		Matrix<CellConfidenceState> oldCells,
		FieldPlayerPov oldSource,
		FieldPlayerPov newSource
	) {
		Collection<MatrixCoordinate> toConsider = new HashSet<>();
		Map<MatrixCoordinate, CellConfidenceState> modifiedCells = new HashMap<>();
		oldCells.getDimension().forEachCoordinate(coordinate -> {
			if (!oldSource.getCells().get(coordinate).isOpen()){
				CellPlayerPov newCell = newSource.getCells().get(coordinate);
				if (newCell instanceof CellPlayerPov.Open){
					toConsider.add(coordinate);
					int neighborMineNumber = ((CellPlayerPov.Open) newCell).getNeighborMineNumber();
					modifiedCells.put(
						coordinate,
						new CellConfidenceState.Open(new Cell.Free(neighborMineNumber))
					);
					for (MatrixCoordinate neighbor : coordinate.getNeighbors(oldCells.getDimension())){
						if (newSource.getCells().get(neighbor).isOpen()){
							toConsider.add(neighbor);
						}
					}
				}
			}
		});
		if (toConsider.isEmpty()){
			return oldCells;
		}
		do {
			Iterator<MatrixCoordinate> iterator = toConsider.iterator();
			MatrixCoordinate coordinate = iterator.next();
			iterator.remove();
			getNewestState(coordinate, oldCells, modifiedCells).fold(
				unknown -> {
					throw new IllegalStateException(String.format(
						"Unexpected operation with an unknown cell at %s",
						coordinate
					));
				},
				mined -> coordinate.getNeighbors(oldCells.getDimension()).stream()
					.filter(c -> getNewestState(c, oldCells, modifiedCells).isOpen())
					.forEach(toConsider::add),
				free -> coordinate.getNeighbors(oldCells.getDimension()).stream()
					.filter(c -> getNewestState(c, oldCells, modifiedCells).isOpen())
					.forEach(toConsider::add),
				open -> {
					Collection<MatrixCoordinate> neighbors = coordinate.getNeighbors(oldCells.getDimension());
					Map<Boolean, List<MatrixCoordinate>> partition =
						neighbors.stream().collect(Collectors.partitioningBy(
							c -> getNewestState(c, oldCells, modifiedCells).isUnknown()
						));
					Collection<MatrixCoordinate> unknownNeighbors = partition.get(true);
					if (unknownNeighbors.isEmpty()){
						return;
					}
					Collection<MatrixCoordinate> knownNeighbors = partition.get(false);
					int neighborMineNumber = (int) knownNeighbors.stream()
						.filter(c -> getNewestState(c, oldCells, modifiedCells).isMined())
						.count();
					if (neighborMineNumber == open.getNeighborMineNumber()){
						unknownNeighbors.forEach(c -> modifiedCells.put(c, CellConfidenceState.Free.INSTANCE));
					} else if (
						knownNeighbors.size() - neighborMineNumber == neighbors.size() - open.getNeighborMineNumber()
					){
						unknownNeighbors.forEach(c -> modifiedCells.put(c, CellConfidenceState.Mined.INSTANCE));
					} else {
						return;
					}
					toConsider.addAll(unknownNeighbors);
				}
			);
		} while (!toConsider.isEmpty());
		return Matrix.create(
			oldCells.getDimension(),
			coordinate -> getNewestState(coordinate, oldCells, modifiedCells),
			CellConfidenceState.class
		);
	}

	private static CellConfidenceState getNewestState(
		MatrixCoordinate coordinate,
		Matrix<CellConfidenceState> oldCells,
		Map<MatrixCoordinate, CellConfidenceState> modifiedCells
	){
		CellConfidenceState modifiedCell = modifiedCells.get(coordinate);
		return modifiedCell == null ? oldCells.get(coordinate) : modifiedCell;
	}

	@Override
	public int hashCode() {
		return cells.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this
			|| obj != null && obj.getClass().equals(getClass()) && ((FieldConfidenceState) obj).cells.equals(cells);
	}
}
