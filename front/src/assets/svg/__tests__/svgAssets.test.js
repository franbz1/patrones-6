import { describe, expect, it } from 'vitest'
import * as assets from '../index'

const requiredKeys = [
  'coin',
  'baseHealth',
  'play',
  'pause',
  'nextStep',
  'reconnect',
  'connectionOk',
  'connectionWarn',
  'connectionOff',
  'towerBase',
  'enemyBasic',
  'upgradeRapid',
  'upgradeFreeze',
  'upgradeReinforced',
  'projectile',
  'laneTile',
  'gridCellEmpty',
  'gridCellSelected',
  'hudPanel',
  'sidePanel',
]

describe('svg asset index', () => {
  it('exports all required assets', () => {
    for (const key of requiredKeys) {
      expect(assets[key]).toBeTruthy()
    }
  })
})

