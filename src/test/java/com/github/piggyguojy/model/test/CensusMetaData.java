package com.github.piggyguojy.model.test;

import com.github.piggyguojy.parser.excel.structure.annotation.ExcelRow;

@ExcelRow(sheet = 3, columnBegin = 1, rowBegin = 8)
public class CensusMetaData {

  private String local;

  private Integer totalRegistered;
  private Integer totalFamilyRegistered;
  private Integer totalCollectiveRegistered;

  private Integer totalPopulation;
  private Integer totalMalePopulation;
  private Integer totalFeMalePopulation;
  private Double populationGenderRatio;

  private Integer totalFamilyPopulation;
  private Integer totalFamilyMalePopulation;
  private Integer totalFamilyFemalePopulation;
  private Double familyPopulationGenderRatio;

  private Integer totalCollectivePopulation;
  private Integer totalCollectiveMalePopulation;
  private Integer totalCollectiveFemalePopulation;
  private Double familyCollectiveGenderRatio;

  private Double averagePopulationPerRegistered;
}
