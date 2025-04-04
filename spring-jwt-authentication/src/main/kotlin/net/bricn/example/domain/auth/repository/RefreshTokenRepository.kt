package net.bricn.example.domain.auth.repository

import net.bricn.example.domain.auth.entity.RefreshToken
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface RefreshTokenRepository : CrudRepository<RefreshToken, UUID>
