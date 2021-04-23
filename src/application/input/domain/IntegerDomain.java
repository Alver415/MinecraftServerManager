package application.input.domain;

import application.input.InvalidValueException;

public class IntegerDomain extends AbstractDomain<Integer> {

	{
		type = Integer.class;
	}
	
	protected Integer min;
	protected Integer max;
	
	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		if (min != null && max != null && min > max) {
			throw new IllegalArgumentException("min must be less than or equal to max. min=" + min + " max=" + max);
		}
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		if (min != null && max != null && min > max) {
			throw new IllegalArgumentException("max must be greater than or equal to min. max=" + max + " min=" + min);
		}
		this.max = max;
	}

	@Override
	public void validate(Integer value) throws InvalidValueException {
		super.validate(value);
		if (max != null && value != null && max < value) {
			throw new InvalidValueException("max value is " + max);
		}
		if (min != null && value != null && min > value) {
			throw new InvalidValueException("min value is " + min);
		}
	}
}